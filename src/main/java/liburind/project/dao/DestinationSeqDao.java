package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.DestinationSeq;
import liburind.project.model.DestinationSeqKey;

public interface DestinationSeqDao extends JpaRepository<DestinationSeq, DestinationSeqKey> {

}
