package ru.serega6531.pixelwars.creative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.serega6531.pixelwars.creative.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
