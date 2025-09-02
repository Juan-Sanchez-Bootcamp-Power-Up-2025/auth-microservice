package co.com.crediya.authentication.api;

import co.com.crediya.authentication.api.dto.UserRequestDto;
import co.com.crediya.authentication.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveUser",
                    operation = @Operation(
                            operationId = "listenSaveUser",
                            summary = "Register new user",
                            description = "Creates a new user with name, lastname, email, birthdate, address, document id, phone number, salary",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserRequestDto.class),
                                            examples = {
                                                    @ExampleObject(name = "User example",
                                                            value = """
                                                                    {
                                                                        "name": "Name",
                                                                        "lastName": "Last name",
                                                                        "email": "email@crediya.com",
                                                                        "birthDate": "2000-01-01",
                                                                        "address": "Address 25 - 36",
                                                                        "documentId": "12345678",
                                                                        "phoneNumber": "123456",
                                                                        "baseSalary": 12345
                                                                    }
                                                                    """,
                                                            description = "User example to test the registration of an user."
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200", description = "User registered",
                                            content = @Content(schema = @Schema(implementation = User.class),
                                                    examples = {
                                                            @ExampleObject(name = "User example",
                                                                    value = """
                                                                    {
                                                                        "name": "Name",
                                                                        "lastName": "Last name",
                                                                        "email": "email@crediya.com",
                                                                        "birthDate": "2000-01-01",
                                                                        "address": "Address 25 - 36",
                                                                        "documentId": "12345678",
                                                                        "phoneNumber": "123456",
                                                                        "baseSalary": 12345
                                                                    }
                                                                    """,
                                                                    description = "User example to test the registration of an user."
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400", description = "Invalid Data",
                                            content = @Content(schema = @Schema(implementation = List.class),
                                                    examples = {@ExampleObject(
                                                            name = "Mandatory fields",
                                                            value = """
                                                            {
                                                                 "error": "Invalid data",
                                                                 "violations": [
                                                                     {
                                                                         "field": "baseSalary",
                                                                         "message": "salary must be less than 15000000"
                                                                     },
                                                                     {
                                                                         "field": "name",
                                                                         "message": "name is mandatory"
                                                                     },
                                                                     {
                                                                         "field": "lastName",
                                                                         "message": "last name is mandatory"
                                                                     },
                                                                     {
                                                                         "field": "email",
                                                                         "message": "email format is not valid"
                                                                     }
                                                                 ]
                                                            }
                                                            """,
                                                            description = "Bad request for invalid fields."
                                                    ), @ExampleObject(
                                                            name = "Invalid email format",
                                                            value = """
                                                                    {
                                                                         "error": "Invalid data",
                                                                         "violations": [
                                                                             {
                                                                                 "field": "email",
                                                                                 "message": "email format is not valid"
                                                                             }
                                                                         ]
                                                                     }
                                                            """,
                                                            description = "Bad request for invalid email format."
                                                    )}
                                            )
                                    ),
                                    @ApiResponse(responseCode = "500", description = "Internal Error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/users/validate",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = Handler.class,
                    beanMethod = "listenExistsUserByEmailAndDocumentId",
                    operation = @Operation(
                            operationId = "listenExistsUserByEmailAndDocumentId",
                            summary = "Validate user",
                            description = "Validate if a user is registered by its email and document id",
                            parameters = {
                                    @Parameter(
                                            name = "email",
                                            example = "email@crediya.com",
                                            required = true
                                    ),
                                    @Parameter(
                                            name = "documentId",
                                            example = "12345678",
                                            required = true
                                    )
                            },
                            responses =
                                    @ApiResponse(
                                            responseCode = "200", description = "User is registered",
                                            content = @Content(
                                                    examples = {
                                                            @ExampleObject(name = "Response example",
                                                                    value = """
                                                                            {
                                                                                "valid": true
                                                                            }
                                                                    """,
                                                                    description = "User example to test the registration of an user."
                                                            )
                                                    }
                                            )
                                    )
                    )
            )}
    )
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/login"), handler::listenLogin)
                .andRoute(POST("/api/v1/users"), handler::listenSaveUser)
                .andRoute(GET("/api/v1/users/validate"), handler::listenExistsUserByEmailAndDocumentId);
    }

}
