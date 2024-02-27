alice [
    store ! query @!"";
    store ? price @?"";
    Call Interrogation
]

|

store [
    alice ? query@?"";
    alice ! price @!"";
    alice & { "buy": None }//{ "no": None }
]

|

bob [
    Call Interrogation_Bob
]

Interrogation_Bob:
    alice & {
        "split" : Some(
            If expr
            Then
                alice + "yes"@+"";
                End
            Else
                alice + "no"@+"";
                Call Interrogation_Bob
    )} // {
        "cancel" : None
    }

Interrogation :
    If check (x)
    Then
        bob + "split" @+"";
        bob & {
            "yes": Some(
                store+"buy" @+"";
                End
        )} // {
            "no": Some(
                Call Interrogation
        )}
    Else
        bob + "cancel" @+"";
        store + "no" @+"";
        End

