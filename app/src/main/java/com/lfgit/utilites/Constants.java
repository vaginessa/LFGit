package com.lfgit.utilites;

public class Constants {
    public static final String PKG = "com.lfgit/";
    public static final String APP_DIR = "/data/data" + "/" + PKG;
    public static final String FILES_DIR = APP_DIR + "files";
    public static final String USR_DIR = FILES_DIR + "/usr";
    public static final String LIB_DIR = USR_DIR + "/lib";
    public static final String BIN_DIR = USR_DIR + "/bin";
    public static final String REPOS_DIR = APP_DIR + "/repos";
    public static final String GIT_CORE_DIR = FILES_DIR + "/libexec/git-core";

    public enum AddRepo {
        OK(0),
        ADDED(1);

        int value;
        AddRepo(int value) {
            this.value = value;
        }
    }

    public enum RepoTask {
        INIT(0),
        CLONE(1);

        int value;
        RepoTask(int value) {
            this.value = value;
        }
    }
}
