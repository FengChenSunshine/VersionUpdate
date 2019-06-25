package com.duanlu.upload;

/********************************
 * @name Postcard
 * @author 段露
 * @createDate 2019/05/20 12:02
 * @updateDate 2019/05/20 12:02
 * @version V1.0.0
 * @describe 信息.
 ********************************/
public class Postcard {

    public String downloadUrl;
    public boolean isForce;//是否强制更新.
    public String versionCode;//版本号.
    public String versionName;//版本名.
    public String versionDesc;//新版本描述.

    public String saveDir;
    public String saveName;

    private Postcard() {

    }

    public static class Builder {
        private String downloadUrl;
        private boolean isForce;//是否强制更新.
        private String versionCode;//版本号.
        private String versionName;//版本名.
        private String versionDesc;//新版本描述.

        private String saveDir;
        private String saveName;

        public Builder(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public Builder setSaveConfig(String saveDir, String saveName) {
            this.saveDir = saveDir;
            this.saveName = saveName;
            return this;
        }

        public Builder setForce(boolean force) {
            isForce = force;
            return this;
        }

        public Builder setVersionCode(String versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Builder setVersionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder setVersionDesc(String versionDesc) {
            this.versionDesc = versionDesc;
            return this;
        }

        public Postcard build() {
            Postcard postcard = new Postcard();
            postcard.downloadUrl = this.downloadUrl;
            postcard.isForce = this.isForce;//是否强制更新.
            postcard.versionCode = this.versionCode;
            postcard.versionName = this.versionName;
            postcard.versionDesc = this.versionDesc;

            postcard.saveDir = this.saveDir;
            postcard.saveName = this.saveName;
            return postcard;
        }
    }

    @Override
    public String toString() {
        return "Postcard{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", isForce=" + isForce +
                ", saveDir='" + saveDir + '\'' +
                ", saveName='" + saveName + '\'' +
                '}';
    }
}
