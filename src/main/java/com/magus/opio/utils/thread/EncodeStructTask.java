package com.magus.opio.utils.thread;

import com.magus.opio.utils.BytesUtils;
import com.magus.opio.utils.base.ByteUtil;
import com.magus.opio.utils.base.OPTypeUtil;
import lombok.SneakyThrows;
import java.lang.reflect.Field;

/**
 * @ClassName EncodeStructTask
 * @description：
 * @author：liuyi
 * @Date：2021/8/26 9:21
 */
public class EncodeStructTask implements Runnable {

    private Object obj;
    private Field field;

    public EncodeStructTask(Object obj, Field field) {
        this.obj = obj;
        this.field = field;
    }

    @SneakyThrows
    @Override
    public void run() {
    }
}
