package cn.enilu.xuanyuan.core.async;

import cn.enilu.xuanyuan.core.bean.Ret;

/**
 * 一个简单的异步处理器抽象类
 * 使用方法参考cn.enilu.xuanyuan.application.test.AsyncRunner
 * @Author enilu
 * @Date 2021/5/18 10:02
 * @Version 1.0
 */
public  abstract class AbstractAsyncProcessor {

    public abstract  void process();
    public abstract  boolean isFinished();
    public abstract  Ret getResult();
    public Ret execute(){
       process();
        boolean ret = isFinished();
        while(!ret){
            ret = isFinished();
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {

            }
        }
        return getResult();
    }
}
