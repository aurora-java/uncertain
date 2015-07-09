/*
 * Created on 2015年7月3日 下午7:48:35
 * zhoufan
 */
package uncertain.pipe.impl;

import uncertain.pipe.base.IProcessor;

public class DummyProcessor implements IProcessor {

    public DummyProcessor() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Object process(Object source) {
        return source;
    }

}
