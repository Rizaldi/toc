package com.gts.toc.object;

import android.graphics.drawable.Drawable;

/**
 * Created by warsono on 11/04/16.
 */

public class ObjMenu {
    String MenuId;
    String Menutitle;
    String Menusubtitle;
    Drawable MenuImage;
    int BackgroundColor;

    public String getmMenuId() {
        return MenuId;
    }

    public void setmMenuId(String mMenuId) {
        this.MenuId = mMenuId;
    }

    public String getmMenutitle() {
        return Menutitle;
    }

    public void setmMenutitle(String mMenutitle) {
        this.Menutitle = mMenutitle;
    }

    public Drawable getmMenuImage() {
        return MenuImage;
    }

    public void setmMenuImage(Drawable mMenuImage) {
        this.MenuImage = mMenuImage;
    }

    public int getmBackgroundColor() {
        return BackgroundColor;
    }

    public void setmBackgroundColor(int mBackgroundColor) {
        this.BackgroundColor = mBackgroundColor;
    }
}
