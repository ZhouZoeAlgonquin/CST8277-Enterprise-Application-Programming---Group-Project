Hi team,

I need your help regarding the REST API.
I am consistently getting HTTP 404 – Not Found when trying to access the student endpoint.

Here is the URL I am calling:

http://localhost:8080/REST-ACMECollege-Skeleton-0.0.1-SNAPSHOT/api/v1/student


And the server response is:

HTTP Status 404 – The requested resource is not available

What I have already checked on my side:

@ApplicationPath("/api/v1") is correctly set in RestConfig

@Path("student") is correctly set in StudentResource

StudentResource is already registered inside RestConfig#getClasses()

The application deploys successfully in Payara

The context root in Payara is:
/REST-ACMECollege-Skeleton-0.0.1-SNAPSHOT

RestConfig does not contain nested classes or duplicates

I need your help to confirm the following:

Do you have any additional configuration for REST or JAX-RS on your side?
(for example: a different RestConfig, additional scanning settings, or disabled CDI)

Are there any security settings (RolesAllowed / authentication) that block access to the endpoint before it reaches the resource?

Do I need to log in first to access /student?

Is the context root supposed to be something different?
e.g.
/REST-ACMECollege-Skeleton instead of /REST-ACMECollege-Skeleton-0.0.1-SNAPSHOT

Can you please share the exact URL that works on your machine when accessing the student resource?

Has the project been modified with any custom filters, interceptors, or servlet mappings that could override the JAX-RS configuration?

Let me know what configuration you are using or if there is anything specific I should adjust.
Thanks!
