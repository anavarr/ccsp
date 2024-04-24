client [
    server & {
        "left": Some(
            If state
            Then
                server!msg @!"";
                End
            Else
                server?msg @?"";
                End
        )
    }//{
        "right": Some(
            If state
            Then
                server!msg @!"";
                End
            Else
                server?msg @?"";
                End
        )
    }
]