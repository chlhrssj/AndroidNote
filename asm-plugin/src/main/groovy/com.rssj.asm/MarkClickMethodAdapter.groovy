package com.rssj.asm

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter


class MarkClickMethodAdapter extends AdviceAdapter {

    public static final Set<String> lambdaMethod = new HashSet<String>()

    String descriptor
    String className
    String[] interfaces
    boolean isStatic

    MarkClickMethodAdapter(MethodVisitor methodVisitor, int access, String name, String descriptor, String className, String[] interfaces) {
        super(Opcodes.ASM9, methodVisitor, access, name, descriptor)
        this.className = className
        this.interfaces = interfaces
        this.descriptor = descriptor
        this.isStatic = (access & ACC_STATIC) != 0
    }

    @Override
    void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)
        //检测lambda表达式是否跟点击接口匹配
//        for (Object obj: bootstrapMethodArguments) {
//            println(obj.getClass().getName())
//        }
        if (name == 'onClick' && descriptor.split("\\)")[1] == 'Landroid/view/View$OnClickListener;') {
            if (bootstrapMethodArguments.size() == 3
                && bootstrapMethodArguments[0] instanceof Type
                && bootstrapMethodArguments[1] instanceof Handle
                && bootstrapMethodArguments[2] instanceof Type
                && bootstrapMethodArguments[0].toString() == '(Landroid/view/View;)V'
                && bootstrapMethodArguments[2].toString() == '(Landroid/view/View;)V'
            ) {
                Handle bsm = bootstrapMethodArguments[1]
                println(bsm.owner + '.' + bsm.name + bsm.descriptor)
                lambdaMethod.add((bsm.owner + '.' + bsm.name + bsm.descriptor).toString())
            }
        }
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        if (interfaces != null
                && interfaces.contains('android/view/View$OnClickListener')
                && name == 'onClick'
                && descriptor == '(Landroid/view/View;)V'
        ) {
            //实现了点击接口的类，匹配点击方法，加入点击事件埋点
            mv.visitMethodInsn(INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
            mv.visitInsn(DUP)
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
            mv.visitLdcInsn("$className.".toString())
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
            mv.visitVarInsn(ALOAD, 1)
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackClick", "(Ljava/lang/String;)V", false)
        } else {
            //检测Lambda表达式生成的方法且属于点击方法，加入点击事件埋点
            String currHandle = (className + '.' + name + descriptor).toString()
            if (lambdaMethod.contains(currHandle)) {
                lambdaMethod.remove(currHandle)
//                println(currHandle)
                if (isStatic) {
                    mv.visitMethodInsn(INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
                    mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
                    mv.visitInsn(DUP)
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
                    mv.visitLdcInsn("$className.".toString())
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackClick", "(Ljava/lang/String;)V", false)
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
                    mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
                    mv.visitInsn(DUP)
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
                    mv.visitLdcInsn("$className.".toString())
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                    mv.visitVarInsn(ALOAD, 1)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
                    mv.visitMethodInsn(INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackClick", "(Ljava/lang/String;)V", false)
                }
            }
        }
    }

}