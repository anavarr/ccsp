client [ End ]

MyProcedure :
    client &
    { "left":
        Some(
            client!hello @! "";
            End
        )
    }
    //
    { "right":
        Some(
            client!hola @! "";
            End
        )
    }