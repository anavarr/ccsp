service[
    client+"login" @+ "";
    authenticator &
    {
        "auth": Some(authenticator?result @?""; End)
    }
    //
    {
        "quit" : None
    }
]
|
client[
    service &
    {
        "login": Some(authenticator+"password" @+""; authenticator!password @!""; End)
    }
    //
    {
        "cancel": Some(authenticator+"quit" @+"";End)
    }
]
|
authenticator[
    client &
    {
        "password": Some(client?password @?""; service+"auth" @+""; service!result @!""; End)
    }
    //
    {
        "quit": Some(service+"quit" @+""; End)
    }
]