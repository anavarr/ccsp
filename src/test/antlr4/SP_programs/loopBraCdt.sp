alice [
    Call Interrogation
]

Interrogation :
        bob + "split" @+"";
        bob & {
            "yes": None
        } // {
            "no": Some(
                If e
                    Then
                        Call Interrogation
                    Else
                        End
        )}

