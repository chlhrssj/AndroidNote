package com.rssj.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Activity生命周期处理类
 */
class ActLifecycleHandle {

    public static final ACT_NAME_ON_CREATE = "onCreate"
    public static final ACT_DESC_ON_CREATE = "(Landroid/os/Bundle;)V"

    public static final ACT_NAME_ON_START = "onStart"
    public static final ACT_DESC_ON_START = "()V"

    public static final ACT_NAME_ON_RESUME = "onResume"
    public static final ACT_DESC_ON_RESUME = "()V"

    public static final ACT_NAME_ON_PAUSE = "onPause"
    public static final ACT_DESC_ON_PAUSE = "()V"

    public static final ACT_NAME_ON_STOP = "onStop"
    public static final ACT_DESC_ON_STOP = "()V"

    public static final ACT_NAME_ON_DESTROY = "onDestroy"
    public static final ACT_DESC_ON_DESTROY = "()V"

    int[] lifeEvent = [0,0,0,0,0,0]

    /**
     * 根据方法名和方法描述判断是否属于Activity生命周期回调函数
     * @param methodName 方法名
     * @param methodDesc 方法描述
     * @return 如果是返回 true 并记录该生命周期已经处理过了，否则返回false
     */
    boolean isHandle(String methodName, String methodDesc) {
        if (lifeEvent[0] == 0 && ACT_NAME_ON_CREATE == methodName && ACT_DESC_ON_CREATE == methodDesc) {
            lifeEvent[0] = 1
            return true
        } else if (lifeEvent[1] == 0 && ACT_NAME_ON_START == methodName && ACT_DESC_ON_START == methodDesc) {
            lifeEvent[1] = 1
            return true
        } else if (lifeEvent[2] == 0 && ACT_NAME_ON_RESUME == methodName && ACT_DESC_ON_RESUME == methodDesc) {
            lifeEvent[2] = 1
            return true
        } else if (lifeEvent[3] == 0 && ACT_NAME_ON_PAUSE == methodName && ACT_DESC_ON_PAUSE == methodDesc) {
            lifeEvent[3] = 1
            return true
        } else if (lifeEvent[4] == 0 && ACT_NAME_ON_STOP == methodName && ACT_DESC_ON_STOP == methodDesc) {
            lifeEvent[4] = 1
            return true
        }
        else if (lifeEvent[5] == 0 && ACT_NAME_ON_DESTROY == methodName && ACT_DESC_ON_DESTROY == methodDesc) {
            lifeEvent[5] = 1
            return true
        }
        return false
    }

    /**
     * 当Activity没有重写生命周期回调函数的时候，无法通过visitMethod插桩，需要生成方法主动插桩
     */
    void supplementEvent(ClassVisitor cv) {
        if (lifeEvent[0] == 0) {
            //重写onCreate方法
//            println("supplementEvent -- onCreate")
            lifeEvent[0] = 1
            supplementOnCreate(cv)
        }
        if (lifeEvent[1] == 0) {
            //重写onStart方法
//            println("supplementEvent -- onStart")
            lifeEvent[1] = 1
            supplementOnStart(cv)
        }
        if (lifeEvent[2] == 0) {
            //重写onResume方法
//            println("supplementEvent -- onResume")
            lifeEvent[2] = 1
            supplementOnResume(cv)
        }
        if (lifeEvent[3] == 0) {
            //重写onPause方法
//            println("supplementEvent -- onPause")
            lifeEvent[3] = 1
            supplementOnPause(cv)
        }
        if (lifeEvent[4] == 0) {
            //重写onStop方法
//            println("supplementEvent -- onStop")
            lifeEvent[4] = 1
            supplementOnStop(cv)
        }
        if (lifeEvent[5] == 0) {
            //重写onDestroy方法
//            println("supplementEvent -- onDestroy")
            lifeEvent[5] = 1
            supplementOnDestroy(cv)
        }
    }

    private void supplementOnCreate(ClassVisitor cv) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PROTECTED, "onCreate", "(Landroid/os/Bundle;)V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onCreate -- begin")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onCreate", "(Landroid/os/Bundle;)V", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onCreate -- end")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    private void supplementOnStart(ClassVisitor cv) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PROTECTED, "onStart", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onStart -- begin")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onStart", "()V", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onStart -- end")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    private void supplementOnResume(ClassVisitor cv) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PROTECTED, "onResume", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onResume -- begin")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onResume", "()V", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onResume -- end")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    private void supplementOnPause(ClassVisitor cv) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PROTECTED, "onPause", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onPause -- begin")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onPause", "()V", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onPause -- end")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    private void supplementOnStop(ClassVisitor cv) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PROTECTED, "onStop", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onStop -- begin")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onStop", "()V", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onStop -- end")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    private void supplementOnDestroy(ClassVisitor cv) {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PROTECTED, "onDestroy", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onDestroy -- begin")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onDestroy", "()V", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, "com/rssj/asm/sdk/PointMarkManager", "getInstance", "()Lcom/rssj/asm/sdk/PointMarkManager;", false)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false)
        methodVisitor.visitLdcInsn("onDestroy -- end")
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/rssj/asm/sdk/PointMarkManager", "trackLifecycle", "(Ljava/lang/String;Ljava/lang/String;)V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

}