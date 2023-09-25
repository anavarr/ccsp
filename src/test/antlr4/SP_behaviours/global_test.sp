client ? req @? "" ;
auth ! req.password @!"";
auth ? success @?"";
If success
    Then
        database ! req.query @!"";
        database ? res @?"";
        client!res @!"";
        End
    Else
        client!ko@!"";
        End