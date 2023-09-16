package com.se233.photoeditor.models;

public class ImageFile {
    private String name;
    private String path;
    private String type;
    private long size;

    public ImageFile(String name, String path, String type, long size) {
        this.name = name;
        this.path = path;
        if (!type.equals("png") && !type.equals("jpg") && !type.equals("jpeg")) {
            throw new IllegalArgumentException("Invalid Extension");
        } else {
            this.type = type;
        }
        this.size = size;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ImageFile{" + "name='" + name + '\'' + ", path='" + path + '\'' + ", type='" + type + '\'' + ", size=" + size + '}';
    }
}
