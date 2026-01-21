package com.Back.domain.post.document;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Document(indexName="posts")
@Setting(settingPath = "/elasticsearch/settings.json")
@Getter
public class Post {

    @Id
    private String id;

    @Field(type= FieldType.Text, analyzer = "bigram_analyzer", searchAnalyzer = "bigram_analyzer")
    @Setter
    private String title;
    @Field(type= FieldType.Text, analyzer = "bigram_analyzer", searchAnalyzer = "bigram_analyzer")
    @Setter
    private String content;

    @Field(type= FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Date,format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;
    @Field(type = FieldType.Date,format = DateFormat.date_hour_minute_second_millis)
    @Setter
    private LocalDateTime updatedAt;

    public Post( String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
