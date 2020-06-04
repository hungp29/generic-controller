package org.example.genericcontroller.app.publisher;

import org.example.genericcontroller.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
}
