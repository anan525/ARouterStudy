package com.dndc.arouter

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(RouterConstants.annotationPath)
@SupportedOptions(RouterConstants.MODULE_NAME)
class ARouterProcessor : AbstractProcessor() {

    var messager: Messager? = null
    var typeUtils: Types? = null
    var filer: Filer? = null
    var elementUtils: Elements? = null
    var moduleName: String? = null

    private val routBeanList: ArrayList<RouterBean> = ArrayList()

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

        messager?.printMessage(Diagnostic.Kind.NOTE, "初始化module....")
    }

    override fun process(
        annotationSet: MutableSet<out TypeElement>?,
        roundEnvironment: RoundEnvironment?
    ): Boolean {
        annotationSet?.let {

            if (moduleName.isNullOrEmpty()) {
                throw IllegalStateException(
                    "未配置${RouterConstants.MODULE_NAME},请在build.gradle里面配置，配置如下 kapt {\n" +
                            "            arguments {\n" +
                            "                arg(\"moduleName\": project.getName())\n" +
                            "            }\n" +
                            "        }"
                )
            }

            roundEnvironment?.getElementsAnnotatedWith(ARouter::class.java)
                ?.forEachIndexed { index, element ->
                    val routerType = element.asType()
                    val activityType =
                        elementUtils?.getTypeElement(RouterConstants.ACTIVITYTYPE)?.asType()

                    messager?.printMessage(
                        Diagnostic.Kind.NOTE,
                        "标有注解的类有：" + element.simpleName.toString()
                    )
                    val aRouter = element.getAnnotation(ARouter::class.java)
                    //路由地址
                    val path = aRouter.path
                    var type: RouterBean.TYPE? = null
                    when {
                        typeUtils!!.isSubtype(routerType, activityType) -> {
                            type = RouterBean.TYPE.ACITIVITY
                        }
                        else -> {
                            throw IllegalStateException("目前不支持注解在该类上")
                        }
                    }
                    val routerBean = RouterBean().build(path, element.asType().toString(), type)
                    routBeanList.add(routerBean)
                }
        }
        //生成ARouter_Path_modulename
        createPathFile()
        return true
    }

    private fun createPathFile() {

        val returnType = HashMap::class.java.asClassName().parameterizedBy(
            String::class.asClassName(),
            RouterBean::class.asClassName()
        )

        val funSpec = FunSpec.builder("loadPath").addModifiers(KModifier.OVERRIDE)
            .returns(returnType)
            .addStatement(
                "val %N:%T<%T,%T> = %T()",
                "hashMap",
                HashMap::class,
                String::class,
                RouterBean::class,
                HashMap::class
            )
        routBeanList.forEachIndexed { index, routerBean ->
            funSpec.addStatement(
                "%N.put(\"${routerBean.path}\",%T().build(\"${routerBean.path}\",${routerBean.clazzPath}::class.java,%T.%L))",
                "hashMap",
                RouterBean::class,
                RouterBean.TYPE.ACITIVITY::class,
                routerBean.type
            )
        }
        funSpec.addStatement("return %N", "hashMap")


        val typeSpec = TypeSpec.classBuilder(RouterConstants.buildFileForPathName + moduleName)
            .addSuperinterface(PathLoadListener::class)
            .addFunction(funSpec.build())

        try {
            FileSpec.builder(
                RouterConstants.packageName,
                RouterConstants.buildFileForPathName + moduleName
            )
                .addType(typeSpec.build())
                .build()
                .writeTo(filer!!)
        } catch (e: Exception) {

        }
    }
}