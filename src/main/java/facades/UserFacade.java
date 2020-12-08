package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.Messages;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import security.errorhandling.AuthenticationException;

/** The UserFacade handles the communication with the database.
 * It supports creating, deletion, edit, and retrieval of users.
 * @author Daniel, Emil, Jannich, Jimmy
 */

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private static final Messages MESSAGES = new Messages();

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException(MESSAGES.INVALID_USERNAME_OR_PWD);
            }
        } finally {
            em.close();
        }
        return user;
    }

    /**
     * createUser has the purpose of registering a user to the our database, and adds
     * a "user" role by default, to the registered user.
     * @param username comes from the user. It is retrieved as a request, from our REST endpoint (register).
     * @param password is also a request field from our REST endpoint (register).
     * @param passwordCheck does the same thing, but is a repetition to make sure the passwords are equal.
     * @return
     */
    public UserDTO createUser(String username, String password, String passwordCheck) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user != null) {
                throw new AuthenticationException(MESSAGES.USERNAME_ALREADY_EXISTS);
            } else {
                em.getTransaction().begin();
                if (password.equals(passwordCheck)) {
                    user = new User(username, password);
                    Role userRole = new Role("user");
                    user.addRole(userRole);
                    em.persist(user);
                    em.getTransaction().commit();
                } else {
                    throw new UnsupportedOperationException(MESSAGES.PASSWORDS_DONT_MATCH);
                }
            }
        } finally {
            em.close();
        }

        return new UserDTO(user);
    }

    /**
     * deleteUser deletes the user that is specified from the userName, through our REST endpoint.
     * @param userName is used as the primary key in the DB, and is then the one we search by,
     * because it's unique. The field comes from our REST endpoint (delete)
     * @return
     */
    public UserDTO deleteUser(String userName) throws AuthenticationException, API_Exception {
        EntityManager em = emf.createEntityManager();
        User identifyUser = null;
        try {
            em.getTransaction().begin();
            identifyUser = em.find(User.class, userName);

            if (identifyUser == null) {
                throw new NoResultException(MESSAGES.USERNAME_DOESNT_EXIST);
            }
            em.remove(identifyUser);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new UserDTO(identifyUser);
    }

    /**
     * getAllUsers simply has the purpose of retrieving all users from our database,
     * And return the Username (UserDTO) in a List. Only the username is added in,
     * our UserDTO, as we only want the Username to be shown, so we filter it through our UserDTO.
     * @return
     */
    public List<UserDTO> getAllUsers() throws API_Exception {
        EntityManager em = emf.createEntityManager();
        List<User> allUsers = new ArrayList();
        List<UserDTO> allUsersDTO = new ArrayList();

        try {
            allUsers = em.createNamedQuery("User.getAllRows").getResultList();
            if (allUsers.isEmpty() || allUsers == null) {
                throw new API_Exception(MESSAGES.NO_USERS_FOUND, 404);
            }

            for (User user : allUsers) {
                allUsersDTO.add(new UserDTO(user));
            }

            return allUsersDTO;
        } finally {
            em.close();
        }
    }

    /**
     * editUser has the purpose of editing a password in the database, to an user.
     * @param username This is used to identify the user we will be updating the password for. Gets retrieved from our Endpoint.
     * @param editedPassword This the password we will be changing the user to, in the DB. Gets retrieved from our Endpoint.
     * @return
     */
    public UserDTO editUser(String username, String editedPassword) {
        EntityManager em = emf.createEntityManager();
        User user = null;
        try {
            em.getTransaction().begin();
            user = em.find(User.class, username);

            if (user == null) {
                throw new NoResultException(MESSAGES.USERNAME_DOESNT_EXIST);
            }
            if (!editedPassword.isEmpty()) {
                user.setUserPass(editedPassword);
            }
            
            em.getTransaction().commit();

        } finally {
            em.close();
        }

        return new UserDTO(user);
    }

}
