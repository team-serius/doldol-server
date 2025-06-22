package doldol_server.doldol.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByLoginIdAndIsDeletedFalse(String loginId);

	Optional<User> findBySocialId(String socialId);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);

	boolean existsByNameAndEmailAndPhone(String name, String email, String phone);

	User findByEmail(String email);
}
