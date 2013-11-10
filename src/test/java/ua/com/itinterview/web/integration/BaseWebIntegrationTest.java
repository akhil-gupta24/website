package ua.com.itinterview.web.integration;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.com.itinterview.dao.*;
import ua.com.itinterview.entity.*;
import ua.com.itinterview.web.security.AuthenticationUtils;

import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:test-web-context.xml"})
public abstract class BaseWebIntegrationTest extends
        AbstractTransactionalJUnit4SpringContextTests implements BaseWebIntegrationTestConstants{

    protected MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private InterviewDao interviewDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private PositionDao positionDao;

    @Autowired
    private TechnologyDao technologyDao;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @After
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    protected MockHttpServletRequestBuilder registerUser(){
        return registerUser(USER_NAME, NAME, EMAIL_ANOTHER, PASSWORD, PASSWORD);
    }

    protected MockHttpServletRequestBuilder registerUser(String userName,
                                                         String name, String email, String password, String confirmPassword) {
        return registerUser(userName, name, email, password, confirmPassword, UserEntity.Sex.MALE);
    }

    protected MockHttpServletRequestBuilder registerUser(String userName,
                                                         String name, String email, String password, String confirmPassword, UserEntity.Sex sex) {
        return post("/register").param("userName", userName)
                .param("name", name).param("email", email)
                .param("password", password).param("sex", sex.toString())
                .param("confirmPassword", confirmPassword);
    }

    protected MockHttpServletRequestBuilder loginUser(){
        return loginUser(USER_NAME, PASSWORD);
    }

    protected MockHttpServletRequestBuilder loginUser(String userName,
                                                      String password) {
        return post("/j_spring_security_check")
                .param("j_username", userName)
                .param("j_password", password)
                .param("_spring_security_remember_me", "on");
    }

    protected MockHttpServletRequestBuilder logout(MockHttpSession session) throws Exception {
        return (post("/j_spring_security_logout").session(session));
    }

    @Rollback
    protected UserEntity createUser() throws Exception {
        return createUser(USER_NAME, NAME, EMAIL, PASSWORD);
    }

    @Rollback
    protected UserEntity createUser(String userName, String name, String email, String password) throws Exception {
        return createUser(userName, name, email, password, UserEntity.Sex.FEMALE);
    }

    @Rollback
    protected UserEntity createUser(String userName, String name, String email, String password, UserEntity.Sex sex) throws Exception {
        UserEntity userToSave = new UserEntity();
        userToSave.setUserName(userName);
        userToSave.setName(name);
        userToSave.setEmail(email);
        userToSave.setPassword(authenticationUtils.getMD5Hash(password));
        userToSave.setSex(sex);
        return userDao.save(userToSave);
    }

    @Rollback
    protected InterviewEntity createInterview() throws Exception {
        InterviewEntity interview = new InterviewEntity();
        interview.setUser(createUser());
        interview.setCreated(new Date());
        interview.setCity(createCity());
        interview.setCompany(createCompany());
        interview.setFeedback(USER_FEEDBACK);
        interview.setPosition(createPosition());
        interview.setTechnology(createTechnology());
        return interviewDao.save(interview);
    }

    @Rollback
    protected PositionEntity createPosition(){
        PositionEntity position = new PositionEntity();
        position.setPositionName(POSITION_NAME);
        position.setPositionGroup(POSITION_GROUP);
        return positionDao.save(position);
    }

    @Rollback
    protected TechnologyEntity createTechnology(){
        TechnologyEntity technology = new TechnologyEntity();
        technology.setTechnologyName(TECHNOLOGY_NAME);
        return technologyDao.save(technology);
    }

    @Rollback
    protected CityEntity createCity(){
        CityEntity city = new CityEntity();
        city.setCityName(CITY_NAME);
        return cityDao.save(city);
    }

    @Rollback
    protected CompanyEntity createCompany(){
        CompanyEntity company = new CompanyEntity();
        company.setCompanyName(COMPANY_NAME);
        company.setCompanyAddress(COMPANY_ADDRESS);
        company.setCompanyPhone(COMPANY_PHONE_NUMBER);
        company.setCompanyLogoUrl(COMPANY_LOGO);
        company.setCompanyWebPage(COMPANY_WEB_PAGE);
        company.setType(COMPANY_TYPE);
        return companyDao.save(company);
    }

    public static Cookie[] initCookies() {
        return new Cookie[]{
                new Cookie("MY-COOKIE", "DEFAULT_COOKIE_VALUE")
        };
    }

    public static Cookie[] updateCookies(Cookie[] current,
                                         ResultActions result) {
        Map<String, Cookie> currentCookies = new HashMap<String, Cookie>();
        if (current != null) {
            for (Cookie c : current) {
                currentCookies.put(c.getName(), c);
            }
        }

        Cookie[] newCookies = result.andReturn().getResponse().getCookies();
        for (Cookie newCookie : newCookies) {
            if (StringUtils.isBlank(newCookie.getValue())) {
                currentCookies.remove(newCookie.getName());
            } else {
                currentCookies.put(newCookie.getName(), newCookie);
            }
        }
        return currentCookies.values().toArray(new Cookie[currentCookies.size()]);
    }
}
