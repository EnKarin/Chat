package ru.shift.chat.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.io.IOException;

@Entity
@Table(name = "attach")
public class Attach {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String name;

    @Column(length = 102400)
    @Lob
    private byte[] data;

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
