package com.fiap.siaes.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.siaes.auth.application.usecase.AuthenticateUserUseCase.AuthenticateUserCommand;
import com.fiap.siaes.auth.domain.security.TokenIssuer;
import com.fiap.siaes.auth.infrastructure.web.AuthenticationController;
import com.fiap.siaes.customer.domain.model.vo.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.inventory.domain.repository.StockItemRepository;
import com.fiap.siaes.inventory.domain.repository.StockMovementRepository;
import com.fiap.siaes.maintenance.domain.repository.MaintenanceRepository;
import com.fiap.siaes.quotation.domain.repository.QuotationRepository;
import com.fiap.siaes.sk.infrastructure.config.AsyncConfig;
import com.fiap.siaes.supplies.domain.repository.SuppliesRepository;
import com.fiap.siaes.user.UserTestFactory;
import com.fiap.siaes.user.domain.model.User;
import com.fiap.siaes.user.domain.model.enums.UserRole;
import com.fiap.siaes.user.domain.model.vo.Password;
import com.fiap.siaes.user.domain.repository.UserRepository;
import com.fiap.siaes.vehicle.domain.repository.VehicleRepository;
import com.fiap.siaes.workorder.domain.repository.WorkOrderRepository;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.Executor;

import static com.fiap.siaes.utils.TestFactory.persist;
import static java.util.Objects.nonNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "it"})
public abstract class IntegrationTestUtils {

    protected static final String PASSWORD = "Senha@123";

    private static final String TABLES_QUERY = """
            SELECT schemaname || '.' || tablename FROM pg_tables
            WHERE schemaname = 'siaes' AND tablename <> 'flyway_schema_history'
            """;

    @BeforeEach
    void cleanDatabase() {
        var tables = this.jdbcTemplate.queryForList(TABLES_QUERY, String.class);

        this.transactionTemplate.executeWithoutResult(_ ->
                this.jdbcTemplate.execute("TRUNCATE TABLE " + String.join(", ", tables) + " CASCADE"));
    }

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected JavaMailSender javaMailSender;

    @TestBean(name = AsyncConfig.DOMAIN_EVENT_EXECUTOR)
    private Executor domainEventExecutor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenIssuer tokenIssuer;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected VehicleRepository vehicleRepository;

    @Autowired
    protected SuppliesRepository suppliesRepository;

    @Autowired
    protected StockItemRepository stockItemRepository;

    @Autowired
    protected StockMovementRepository stockMovementRepository;

    @Autowired
    protected MaintenanceRepository maintenanceRepository;

    @Autowired
    protected WorkOrderRepository workOrderRepository;

    @Autowired
    protected QuotationRepository quotationRepository;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    static Executor domainEventExecutor() {
        return new SyncTaskExecutor();
    }

    protected String loginAs(UserRole role) {
        return this.loginAs(role, null);
    }

    protected String loginAs(UserRole role, CustomerId customerId) {
        return this.authenticate(this.persistUser(role, customerId));
    }

    protected User persistUser(UserRole role, CustomerId customerId) {
        return persist(this.userRepository,
                       UserTestFactory.createUser(role, customerId, Password.fromHash(this.passwordEncoder.encode(PASSWORD))));
    }

    protected String authenticate(User user) {
        return this.tokenIssuer.issueAccessToken(user.getId(), user.getRole());
    }

    protected static ResultMatcher jsonValue(String path, Object expectedValue) {
        return jsonPath(path).value(expectedValue);
    }

    protected static <T> ResultMatcher jsonValue(String path, Matcher<? super T> matcher) {
        return jsonPath(path).value(matcher);
    }

    protected String login(String email, String password, String tokenPath) {
        try {
            var result = this.executePost(AuthenticationController.ENDPOINT + "/login", null,
                                          AuthenticateUserCommand.of(email, password))
                    .andExpect(status().isOk())
                    .andReturn();

            return JsonPath.read(result.getResponse().getContentAsString(), tokenPath);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    public ResultActions executeGet(String url, String token) throws Exception {
        return this.perform(get(url), token, null);
    }

    public ResultActions executePost(String url, String token, Object body) throws Exception {
        return this.perform(post(url), token, body);
    }

    public ResultActions executePut(String url, String token, Object body) throws Exception {
        return this.perform(put(url), token, body);
    }

    public ResultActions executePatch(String url, String token, Object body) throws Exception {
        return this.perform(patch(url), token, body);
    }

    public ResultActions executeDelete(String url, String token) throws Exception {
        return this.perform(delete(url), token, null);
    }

    protected String createdId(ResultActions resultActions) throws Exception {
        MvcResult result = resultActions.andExpect(status().isCreated()).andReturn();
        var location = result.getResponse().getHeader(HttpHeaders.LOCATION);

        return location.substring(location.lastIndexOf('/') + 1);
    }

    protected <T> T read(ResultActions resultActions, String path) throws Exception {
        return JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(), path);
    }

    private ResultActions perform(MockHttpServletRequestBuilder request, String token, Object body) throws Exception {
        request.contentType(MediaType.APPLICATION_JSON);

        if (nonNull(token)) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }

        if (nonNull(body)) {
            request.content(this.objectMapper.writeValueAsString(body));
        }

        return this.mockMvc.perform(request);
    }
}
