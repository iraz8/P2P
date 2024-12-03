package com.razvan.client.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileMetadata {
    private String filename;
    private long filesize;
    private String fileformat;

}
