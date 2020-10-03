package qlik.qliktest.repository

import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import qlik.qliktest.entity.MessageEntity

@EnableScan
@Repository
interface MessageRepository : CrudRepository<MessageEntity, String>