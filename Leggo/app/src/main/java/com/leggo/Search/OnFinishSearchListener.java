package com.leggo.Search;

import java.util.List;

/**
 * Created by user on 2015-01-28.
 */
public interface OnFinishSearchListener {
    public void onSuccess(List<Item> itemList);
    public void onFail();
}
