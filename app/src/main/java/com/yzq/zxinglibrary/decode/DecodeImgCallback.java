package com.yzq.zxinglibrary.decode;

import com.google.zxing.Result;

import java.io.IOException;

/**
 * Created by yzq on 2017/10/18.
 *
 * 解析图片的回调
 */

public interface DecodeImgCallback {
    public void onImageDecodeSuccess(Result result) throws IOException;

    public void onImageDecodeFailed();
}
