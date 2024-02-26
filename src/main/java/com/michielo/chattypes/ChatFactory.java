package com.michielo.chattypes;

import com.michielo.chattypes.impl.DefaultMethod;
import com.michielo.chattypes.impl.DefaultRangedMethod;
import com.michielo.chattypes.impl.LuckpermsRangedMethod;
import com.michielo.chattypes.impl.LuckpermsSimpleMethod;

public class ChatFactory {

    private ChatMethod method;

    public ChatMethod getMethod(ChatTypes chatType) {

        if (method != null) return method;
        if (chatType.equals(ChatTypes.DEFAULT)) {
            method = new DefaultMethod();
        } else if (chatType.equals(ChatTypes.DEFAULT_RANGED)) {
            method = new DefaultRangedMethod();
        } else if (chatType.equals(ChatTypes.LUCKPERMS)) {
            method = new LuckpermsSimpleMethod();
        } else if (chatType.equals(ChatTypes.LUCKPERMS_RANGED)) {
            method = new LuckpermsRangedMethod();
        }
        return method;
    }

}
