server [
    client &
    { "left" :
        Some(End)
    }
    //
    { "right" :
        Some(
            client?req @?"";
            client!res @!"";
            End
        )
    }
]