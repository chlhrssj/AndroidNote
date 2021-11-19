package com.rssj.asm

/**
 * Activity生命周期处理类
 * 用于记录当前方法到底是插入还是生成
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

}