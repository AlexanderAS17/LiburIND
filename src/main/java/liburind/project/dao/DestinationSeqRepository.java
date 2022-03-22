package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.DestinationSeq;
import liburind.project.model.DestinationSeqKey;

public interface DestinationSeqRepository extends MongoRepository<DestinationSeq, DestinationSeqKey> {

}
