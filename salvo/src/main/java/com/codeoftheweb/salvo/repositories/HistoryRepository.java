package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface HistoryRepository extends JpaRepository<History,Long>{
}


