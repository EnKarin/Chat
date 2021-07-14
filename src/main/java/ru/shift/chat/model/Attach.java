package ru.shift.chat.model;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;

@Entity
@Table(name = "attach")
public class Attach {

    @ApiModelProperty
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String name;

    @ApiModelProperty
    @Column(length = 102400)
    @Lob
    private byte[] data;

    @ApiModelProperty
    @Column
    private String expansion;

    public void setExpansion(String expansion) {
        this.expansion = expansion;
    }

    public String getExpansion() {
        return expansion;
    }

    public void setData(MultipartFile file) throws IOException {
        this.data = file.getBytes();
    }

    public void setData(byte[] file){
        this.data = file;
    }

    public int getSize(){
        return data.length;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
