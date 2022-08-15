package web.links.test;

import web.links.model.LinkModel;
import web.links.model.UserModel;

import java.time.ZonedDateTime;
import java.util.Map;

public final class TestData {

    private static final ZonedDateTime CREATED_DATE = ZonedDateTime.parse("2022-08-15T15:00:00Z");
    private static final ZonedDateTime MODIFIED_DATE = ZonedDateTime.parse("2022-08-15T16:30:00Z");

    public static final String ALICE_ID = "erLoRCSeAmJuCT11XRt505DsU_3o-HfA";
    public static final String BOB_ID = "u_za87qb06XbDSGH-LTwGABdXW6dQ_Ml";

    public static final UserModel ALICE = new UserModel(0, ALICE_ID, "alice", "");
    public static final UserModel BOB = new UserModel(1, BOB_ID, "bob", "");

    public static final String ALICE_PUBLIC_LINK_ID = "69bBbEs6";
    public static final String ALICE_MODIFIED_LINK_ID = "UkraaBqG";
    public static final String ALICE_PRIVATE_LINK_ID = "AECakbJn";
    public static final String ALICE_DISABLED_LINK_ID = "FDYXirAU";
    public static final String BOB_PUBLIC_LINK_ID = "XTQM1YO0";
    public static final String BOB_MODIFIED_LINK_ID = "rTWcpd8h";
    public static final String BOB_PRIVATE_LINK_ID = "peTJnDuJ";
    public static final String BOB_DISABLED_LINK_ID = "VHcj7dyS";

    public static final LinkModel ALICE_PUBLIC_LINK = new LinkModel(0, ALICE_PUBLIC_LINK_ID, ALICE_ID, false, false, "http://alice.example.com/unit/test/data", "a_public_link", CREATED_DATE, CREATED_DATE);
    public static final LinkModel ALICE_MODIFIED_LINK = new LinkModel(1, ALICE_MODIFIED_LINK_ID, ALICE_ID, false, false, "http://alice.example.com/unit/test/data", "a_modified_link", CREATED_DATE, MODIFIED_DATE);
    public static final LinkModel ALICE_PRIVATE_LINK = new LinkModel(2, ALICE_PRIVATE_LINK_ID, ALICE_ID, true, false, "http://alice.example.com/unit/test/data", "a_private_link", CREATED_DATE, CREATED_DATE);
    public static final LinkModel ALICE_DISABLED_LINK = new LinkModel(3, ALICE_DISABLED_LINK_ID, ALICE_ID, false, true, "http://alice.example.com/unit/test/data", "a_disabled_link", CREATED_DATE, CREATED_DATE);
    public static final LinkModel BOB_PUBLIC_LINK = new LinkModel(4, BOB_PUBLIC_LINK_ID, BOB_ID, false, false, "http://bob.example.com/unit/test/data", "b_public_link", CREATED_DATE, CREATED_DATE);
    public static final LinkModel BOB_MODIFIED_LINK = new LinkModel(4, BOB_MODIFIED_LINK_ID, BOB_ID, false, false, "http://bob.example.com/unit/test/data", "b_modified_link", CREATED_DATE, MODIFIED_DATE);
    public static final LinkModel BOB_PRIVATE_LINK = new LinkModel(4, BOB_PRIVATE_LINK_ID, BOB_ID, true, false, "http://bob.example.com/unit/test/data", "b_private_link", CREATED_DATE, CREATED_DATE);
    public static final LinkModel BOB_DISABLED_LINK = new LinkModel(4, BOB_DISABLED_LINK_ID, BOB_ID, false, true, "http://bob.example.com/unit/test/data", "b_disabled_link", CREATED_DATE, CREATED_DATE);
}
