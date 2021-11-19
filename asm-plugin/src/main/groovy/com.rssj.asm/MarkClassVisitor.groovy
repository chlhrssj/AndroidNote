package com.rssj.asm

import org.objectweb.asm.*

class MarkClassVisitor extends ClassVisitor implements Opcodes {

    private final static String superActivity = "androidx/appcompat/app/AppCompatActivity"


    private final static String superFragment = "androidx/fragment/app/Fragment"

    //SDK类路径
    private final static String SDK_API_CLASS = "com/rssj/asm/sdk/PointMarkManager"


    MarkClassVisitor(final ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor)
        this.classVisitor = classVisitor
    }

    private String[] mInterfaces
    private ClassVisitor classVisitor
    private String currentSuperClassName

    private ActLifecycleHandle actHandle

//    private HashMap<String, MkAnalyticsMethodCell> mLambdaMethodCells = new HashMap<>()


    private
    static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName, String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes[i - start], i)
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false)
    }

    /**
     *  visit 可以拿到关于 .class 的所有信息,比如: 当前类所实现的接口列表
     * @param version JDK的版本
     * @param access 类的修饰符
     * @param name 类的名称
     * @param signature 当前类的签名
     * @param superName 当前类所继承的父类
     * @param interfaces 类所实现的接口列表,在java中,一个类是可以实现多个不同的接口,因此该参数是一个数组类型
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        mInterfaces = interfaces
        currentSuperClassName = superName
        if (superName != null) {
            if (superName == superActivity) {
                println("visit ----> " + version + " " + access + " " + name + " " + signature + " " + superName + " ")
                actHandle = new ActLifecycleHandle()
            } else if (superName == superFragment) {

                println("visit ----> " + version + " " + access + " " + name + " " + signature + " " + superName + " ")

            }
        }
    }

    /**
     * 可以拿到关于 method所有的信息,比如: 方法名 方法的参数描述
     * @param access 方法的修饰符
     * @param name 方法的名称
     * @param desc 方法描述
     * @param signature 方法签名
     * @param exceptions 异常信息
     * @return MethodVisitor
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor visitMethod = super.visitMethod(access, name, desc, signature, exceptions)

        if (currentSuperClassName == superActivity) {
            //如果属于AppCompatActivity的子类，插入生命周期监听代码
            println("method ----> " + access + " " + name + " " + desc + " " + signature + " ")
            if (actHandle.isHandle(name, desc)) {
                visitMethod = new MarkLifecycleMethodAdapter(visitMethod, access, name, desc, name)
            }
        } else if (currentSuperClassName.contains(superFragment)) {
            //如果属于Fragment的子类，插入Fragment生命周期监听代码

        }

        return visitMethod

    }

    @Override
    void visitEnd() {

        cv.visitEnd()
    }
/**
 * 获取方法参数下标为 index 的对应 ASM index
 * @param types 方法参数类型数组
 * @param index 方法中参数下标，从 0 开始
 * @param isStaticMethod 该方法是否为静态方法
 * @return 访问该方法的 index 位参数的 ASM index
 */
    int getVisitPosition(Type[] types, int index, boolean isStaticMethod) {
        if (types == null || index < 0 || index >= types.length) {
            throw new Error("getVisitPosition error")
        }
        if (index == 0) {
            return isStaticMethod ? 0 : 1
        } else {
            return getVisitPosition(types, index - 1, isStaticMethod) + types[index - 1].getSize()
        }
    }

}