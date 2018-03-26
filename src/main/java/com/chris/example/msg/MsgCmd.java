package com.chris.example.msg;

public class MsgCmd {
    public final static int C_INIT  		 	   =  0x10001;      //65537,初始化一个记忆回放单元65537
    public final static int S_INIT  			   =  0x10002;      //65538

    public final static int C_INSERT   				=  0x10003;		//65539,插入一个样本
    public final static int S_INSERT	    		=  0x10004;     //65540

    public final static int C_UPDATE   				   =  0x10005;		//65541,更新样本
    public final static int S_UPDATE	    			   =  0x10006;  //65542

    public final static int C_SAMPLE   				   =  0x10007;		//65543,采样
    public final static int S_SAMPLE	    			   =  0x10008;  //65544

    public final static int C_CLEAN  				   =  0x10009;		//65545,清除
    public final static int S_CLEAN	    			   =  0x1000A;      //65546

    public final static int C_ISREADY                   =  0x1000B;		//65547, 是否ready
    public final static int S_ISREADY                   =  0x1000C;		//65548

    public static void main(String[] args) {
        System.out.println(S_CLEAN);
    }
}
