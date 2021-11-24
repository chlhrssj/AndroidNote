package com.rssj.asm

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

class MarkLifecycleMethodAdapter extends AdviceAdapter {
    String eventName = ""

    MarkLifecycleMethodAdapter(MethodVisitor methodVisitor, int access, String name, String descriptor, String eventName) {
        super(Opcodes.ASM9, methodVisitor, access, name, descriptor)
        this.eventName = eventName
    }

    /**
     * 表示 ASM 开始扫描这个方法
     */
    @Override
    void visitCode() {
        super.visitCode()
    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, desc, isInterface)
    }

    @Override
    void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute)
    }

    /**
     * 在原方法开始之前调用
     */
    @Override
    protected void onMethodEnter() {
//        println("onMethodEnter 被调用")
        super.onMethodEnter()
        mv.visitMethodInsn(INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        mv.visitLdcInsn("$eventName -- begin".toString())
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
    }

    /**
     * 在原方法结束的时候调用
     */
    @Override
    protected void onMethodExit(int opcode) {
//        println("onMethodExit 被调用")
        super.onMethodExit(opcode)
        mv.visitMethodInsn(INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        mv.visitLdcInsn("$eventName -- end".toString())
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
    }

    /**
     * 表示方法输出完毕
     */
    @Override
    void visitEnd() {
        super.visitEnd()
    }

    @Override
    void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc)
    }

    @Override
    void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var, increment)
    }

    @Override
    void visitIntInsn(int i, int i1) {
        super.visitIntInsn(i, i1)
    }

    /**
     * 该方法是 visitEnd 之前调用的方法，可以反复调用。用以确定类方法在执行时候的堆栈大小。
     * @param maxStack
     * @param maxLocals
     */
    @Override
    void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals)
    }


}