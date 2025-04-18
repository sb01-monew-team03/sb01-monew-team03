package team03.monew.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import team03.monew.entity.base.BaseEntity;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

}
