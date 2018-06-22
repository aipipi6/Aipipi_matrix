package com.example.aipipi.base;


/**
 * Created by chenjun on 2017/12/30.
 */

public abstract class BasePresenter<V>{

    protected V mView;

    public void attach(V mView){
        this.mView = mView;
    }

    public void dettach(){
        mView = null;
    }

}
