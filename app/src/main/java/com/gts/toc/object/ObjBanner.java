package com.gts.toc.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by warsono on 11/04/16.
 */

public class ObjBanner {
    @SerializedName("ID")
    private String BannerId;

    @SerializedName("BANNER_IMAGE")
    private String BannerImage;

    @SerializedName("BANNER_LINK")
    private String BannerLink;

    public String getBannerId() {
        return BannerId;
    }

    public void setBannerId(String bannerId) {
        BannerId = bannerId;
    }

    public String getBannerImage() {
        return BannerImage;
    }

    public void setBannerImage(String bannerImage) {
        BannerImage = bannerImage;
    }

    public String getBannerLink() {
        return BannerLink;
    }

    public void setBannerLink(String bannerLink) {
        BannerLink = bannerLink;
    }
}
