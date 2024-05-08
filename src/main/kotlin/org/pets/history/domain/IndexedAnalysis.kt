package org.pets.history.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

@Document(indexName = "analyses")
class IndexedAnalysis {
    @Id
    var id: Long? = null

    @Field(type = FieldType.Long)
    var analysisId: Long? = null

    @Field(type = FieldType.Text)
    var text: String = ""
}
