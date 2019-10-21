package com.intinic.sdk;

import android.os.Parcel;
import android.os.Parcelable;

public class ModelListContent implements Parcelable {
    private String keyId;
    private String keyModelName;
    private String keyAndroidARCoreModelUrl;
    private String keyIosARKitModelUrl;
    private String keyOtherDeviceModelUrl;
    private String keyVersion;
    private String keyImageUrl;
    private String keyScale;
    private String minScale;
    private String maxScale;

    public ModelListContent(String keyId, String keyModelName, String keyAndroidARCoreModelUrl, String keyIosARKitModelUrl, String keyOtherDeviceModelUrl, String keyVersion, String keyImageUrl, String keyScale, String minScale, String maxScale) {
        this.keyId = keyId;
        this.keyModelName = keyModelName;
        this.keyAndroidARCoreModelUrl = keyAndroidARCoreModelUrl;
        this.keyIosARKitModelUrl = keyIosARKitModelUrl;
        this.keyOtherDeviceModelUrl = keyOtherDeviceModelUrl;
        this.keyVersion = keyVersion;
        this.keyImageUrl = keyImageUrl;
        this.keyScale = keyScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyModelName() {
        return keyModelName;
    }

    public void setKeyModelName(String keyModelName) {
        this.keyModelName = keyModelName;
    }

    public String getKeyAndroidARCoreModelUrl() {
        return keyAndroidARCoreModelUrl;
    }

    public void setKeyAndroidARCoreModelUrl(String keyAndroidARCoreModelUrl) {
        this.keyAndroidARCoreModelUrl = keyAndroidARCoreModelUrl;
    }

    public String getKeyIosARKitModelUrl() {
        return keyIosARKitModelUrl;
    }

    public void setKeyIosARKitModelUrl(String keyIosARKitModelUrl) {
        this.keyIosARKitModelUrl = keyIosARKitModelUrl;
    }

    public String getKeyOtherDeviceModelUrl() {
        return keyOtherDeviceModelUrl;
    }

    public void setKeyOtherDeviceModelUrl(String keyOtherDeviceModelUrl) {
        this.keyOtherDeviceModelUrl = keyOtherDeviceModelUrl;
    }

    public String getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
    }

    public String getKeyImageUrl() {
        return keyImageUrl;
    }

    public void setKeyImageUrl(String keyImageUrl) {
        this.keyImageUrl = keyImageUrl;
    }

    public String getKeyScale() {
        return keyScale;
    }

    public void setKeyScale(String keyScale) {
        this.keyScale = keyScale;
    }

    public String getMinScale() {
        return minScale;
    }

    public void setMinScale(String minScale) {
        this.minScale = minScale;
    }

    public String getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(String maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.keyId);
        dest.writeString(this.keyModelName);
        dest.writeString(this.keyAndroidARCoreModelUrl);
        dest.writeString(this.keyIosARKitModelUrl);
        dest.writeString(this.keyOtherDeviceModelUrl);
        dest.writeString(this.keyVersion);
        dest.writeString(this.keyImageUrl);
        dest.writeString(this.keyScale);
        dest.writeString(this.minScale);
        dest.writeString(this.maxScale);
    }

    protected ModelListContent(Parcel in) {
        this.keyId = in.readString();
        this.keyModelName = in.readString();
        this.keyAndroidARCoreModelUrl = in.readString();
        this.keyIosARKitModelUrl = in.readString();
        this.keyOtherDeviceModelUrl = in.readString();
        this.keyVersion = in.readString();
        this.keyImageUrl = in.readString();
        this.keyScale = in.readString();
        this.minScale = in.readString();
        this.maxScale = in.readString();
    }

    public static final Parcelable.Creator<ModelListContent> CREATOR = new Parcelable.Creator<ModelListContent>() {
        @Override
        public ModelListContent createFromParcel(Parcel source) {
            return new ModelListContent(source);
        }

        @Override
        public ModelListContent[] newArray(int size) {
            return new ModelListContent[size];
        }
    };
}