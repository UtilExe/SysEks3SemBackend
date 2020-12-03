package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.Messages;
import java.util.ArrayList;
import java.util.List;
import javassist.tools.rmi.ObjectNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import security.errorhandling.AuthenticationException;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private static final Messages messages = new Messages();

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
                throw new AuthenticationException(messages.invalidUsernameOrPwd);
            }
        } finally {
            em.close();
        }
        return user;
    }

    public UserDTO createUser(String username, String password, String passwordCheck) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user != null) {
                throw new AuthenticationException(messages.usernameAlreadyExists);
            } else {
                em.getTransaction().begin();
                if (password.equals(passwordCheck)) {
                    user = new User(username, password);
                    Role userRole = new Role("user");
                    user.addRole(userRole);
                    // em.persist(userRole);
                    em.persist(user);
                    em.getTransaction().commit();
                } else {
                    throw new UnsupportedOperationException(messages.passwordsNotMatch);
                }
            }
        } finally {
            em.close();
        }

        return new UserDTO(user);
    }

    public UserDTO deleteUser(String userName) throws AuthenticationException, API_Exception {
        EntityManager em = emf.createEntityManager();
        UserDTO identifyUser = null;
        try {
            em.getTransaction().begin();
            identifyUser = em.find(UserDTO.class, userName);

            if (identifyUser == null) {
                throw new NoResultException(messages.usernameDoesntExist);
            }
            em.remove(identifyUser);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return identifyUser;
    }

    public List<UserDTO> getAllUsers() throws ObjectNotFoundException {
        EntityManager em = emf.createEntityManager();
        List<User> allUsers = new ArrayList();
        List<UserDTO> allUsersDTO = new ArrayList();

        try {
            allUsers = em.createNamedQuery("User.getAllRows").getResultList();
            if (allUsers.isEmpty() || allUsers == null) {
                throw new ObjectNotFoundException("No persons found.");
            }

            for (User user : allUsers) {
                allUsersDTO.add(new UserDTO(user));
            }

            return allUsersDTO;
        } finally {
            em.close();
        }
    }

}
