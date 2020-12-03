package facades;

import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.Messages;
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

    public User createUser(String username, String password, String passwordCheck) throws AuthenticationException {
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

        return user;
    }

    public User deleteUser(String userName) throws AuthenticationException, API_Exception {
        EntityManager em = emf.createEntityManager();
        User identifyUser = null;
        try {
            em.getTransaction().begin();
            identifyUser = em.createQuery(
                    "SELECT u from User u WHERE u.userName = :userName", User.class).
                    setParameter("userName", userName).getSingleResult();

            if (identifyUser == null) {
                throw new API_Exception(messages.usernameDoesntExist, 400);
            }
            em.remove(identifyUser);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return identifyUser;
    }
    
}
