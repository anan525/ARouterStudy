package com.dndc.arouter

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(RouterConstants.parameterPath)
@SupportedOptions(RouterConstants.MODULE_NAME)
class ParameterProcessor : AbstractProcessor() {

    var messager: Messager? = null
    var typeUtils: Types? = null
    var filer: Filer? = null
    var elementUtils: Elements? = null
    var moduleName: String? = null
    val parameterMap: HashMap<String, ArrayList<Element>> = HashMap()

    override fun init(processingEnvironment: ProcessingEnvironment?) {
        super.init(processingEnvironment)

        messager = processingEnvironment?.messager
        val options = processingEnvironment?.options
        typeUtils = processingEnvironment?.typeUtils
        filer = processingEnvironment?.filer
        elementUtils = processingEnvironment?.elementUtils
        if (options!!.containsKey(RouterConstants.MODULE_NAME)) {
            moduleName = options?.get(RouterConstants.MODULE_NAME)?.replace("}", "")
        } else if (options!!.containsKey("{" + RouterConstants.MODULE_NAME)) {
            //不知道为啥，key前面多了一个{,value后面多了一个}
            moduleName = options?.get("{" + RouterConstants.MODULE_NAME)?.replace("}", "")
        }


        messager?.printMessage(Diagnostic.Kind.NOTE, "初始化parameter....")
    }


    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        p0?.let {
            p1?.getElementsAnnotatedWith(Parameter::class.java)?.forEachIndexed { index, element ->
                val enclosedElements = element.enclosingElement as TypeElement
                val bestGuess = ClassName.bestGuess(enclosedElements.toString())
                if (parameterMap.containsKey(bestGuess.simpleName)) {
                    val elements = parameterMap.get(bestGuess.simpleName)!!
                    elements.add(element)
                    parameterMap.put(bestGuess.simpleName, elements)
                } else {
                    val arrayList = ArrayList<Element>()
                    arrayList.add(element)
                    parameterMap.put(bestGuess.simpleName, arrayList)
                }
            }
        }
        createParameterFiles()
        return true
    }

    private fun createParameterFiles() {
        if (parameterMap.size > 0) {
            //每個key創建一個文件
            parameterMap.keys.forEach {
                var activityName = it
                val elements = parameterMap.get(it)!!
                createParameterFile(activityName, elements)
            }
        }
    }

    private fun createParameterFile(activityName: String, elements: ArrayList<Element>) {

        if (elements.size == 0) {
            return
        }
        val firstElement = elements.get(0)
        val packageName = elementUtils!!.getPackageOf(firstElement).qualifiedName

        val finaClassName = activityName + RouterConstants.buildFileEndName

        val parameterSpec = ParameterSpec("target", Any::class.asTypeName())

        val bestGuess = ClassName.bestGuess("$packageName." + activityName)


        val funSpec = FunSpec.builder("parameterLoad")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(parameterSpec)
            .addStatement("%N as %T", "target", bestGuess)

        val arouterManagerType = elementUtils!!.getTypeElement(RouterConstants.arouterManagePath)

        val canonicalName = Any::class.java.asClassName().canonicalName


        elements.forEachIndexed { index, element ->
            val parameter = element.getAnnotation(Parameter::class.java)
            var clazzName = element.asType().toString()
            val name = parameter.name
            when {
                element.asType().kind.ordinal == TypeKind.INT.ordinal -> {
                    funSpec.addStatement(
                        "  %N.%N = %N.intent.getIntExtra(\"${parameter.name}\", %N.%N)",
                        "target",
                        element.simpleName,
                        "target",
                        "target",
                        element.simpleName
                    )
                }
                clazzName.equals(RouterConstants.stringType, true) -> {
                    funSpec.addStatement(
                        "   val %N = %N.intent.getStringExtra(\"${parameter.name}\")",
                        "stringElement" + index,
                        "target"
                    )
                    funSpec.addStatement(
                        " %N.${element.simpleName} = if (%N.isNullOrEmpty()) {\n" +
                                "            %N.${element.simpleName}\n" +
                                "        } else {\n" +
                                "            %N!!\n" +
                                "        }",
                        "target",
                        "stringElement" + index,
                        "target",
                        "stringElement" + index
                    )
                }
                //路由形式的
                name.startsWith("/") && name.lastIndexOf("/") != 0 -> {

                    val mapJavaToKotlin =
                        JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(clazzName))
                            ?.asSingleFqName().toString()

                    messager?.printMessage(
                        Diagnostic.Kind.NOTE,
                        "看看类型" + mapJavaToKotlin + "   ," + clazzName + "   " + element.simpleName
                    )

                    val className = try {
                        ClassName.bestGuess(mapJavaToKotlin)
                    } catch (e: Exception) {
                        //没有说明不是基本类型
                        ClassName.bestGuess(clazzName)
                    }

                    if (name.startsWith("/") && name.lastIndexOf("/") != 0) {
                        //从路由里面拿
                        funSpec.addStatement(
                            " val %N = %T.instance.build(\"${parameter.name}\").navigation(%N)",
                            "navigation" + index, arouterManagerType, "target"
                        )
                            .addStatement(
                                "if (%N == null) {\n" +
                                        "            %N.%N = %N.%N\n" +
                                        "        } else {\n" +
                                        "            %N as %T\n" +
                                        "            %N.%N = %N.fromJson(%N.call().toString(), %T::class.java)\n" +
                                        "        }",
                                "navigation" + index,
                                "target",
                                element.simpleName,
                                "target",
                                element.simpleName,
                                "navigation" + index,
                                CallListener::class,
                                "target", element.simpleName, "gson",
                                "navigation" + index, className
                            )
                    }
                }


            }

        }

        val propertySpec =
            PropertySpec.builder("gson", Gson::class).initializer(CodeBlock.of("%T()", Gson::class))
                .addModifiers(KModifier.PRIVATE)

        val typeSpec = TypeSpec.classBuilder(finaClassName)
            .addSuperinterface(ParameterLoadListener::class.java)
            .addProperty(propertySpec.build())
            .addFunction(funSpec.build())

        try {
            FileSpec.builder(RouterConstants.packageName, finaClassName)
                .addType(typeSpec.build())
                .build()
                .writeTo(filer!!)
        } catch (e: Exception) {
        }
    }
}