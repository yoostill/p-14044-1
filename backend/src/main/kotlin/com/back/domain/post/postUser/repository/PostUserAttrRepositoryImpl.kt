package com.back.domain.post.postUser.repository

import com.back.domain.post.postUser.entity.PostUser
import com.back.domain.post.postUser.entity.PostUserAttr
import jakarta.persistence.EntityManager
import org.hibernate.Session

class PostUserAttrRepositoryImpl(
    private val entityManager: EntityManager,
) : PostUserAttrRepositoryCustom {
    override fun findBySubjectAndName(subject: PostUser, name: String): PostUserAttr? {
        return entityManager.unwrap(Session::class.java)
            .byNaturalId(PostUserAttr::class.java)
            .using(PostUserAttr::subject.name, subject)
            .using(PostUserAttr::name.name, name)
            .load()
    }
}