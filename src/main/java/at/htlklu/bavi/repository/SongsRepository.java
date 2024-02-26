package at.htlklu.bavi.repository;

import at.htlklu.bavi.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongsRepository extends JpaRepository<Song, Integer> {
}
