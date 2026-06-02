import { Link } from "react-router-dom";
import * as navStyles from "./NavBarCss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass, faChevronDown } from "@fortawesome/free-solid-svg-icons";

const NavBar = () => {
    return (
        <nav style={navStyles.navStyle}>
            <div style={navStyles.leftSectionStyle}>
                <Link to="/" style={navStyles.logoPlaceholderStyle}>B</Link>
                <div style={navStyles.linkGroupStyle}>
                    <span style={navStyles.linkStyle}>Problems</span>
                    <span style={navStyles.linkStyle}>Contest</span>
                    <span style={navStyles.linkStyle}>Discuss</span>
                    <span style={{ ...navStyles.linkStyle, ...navStyles.dropdownStyle }}>
                        Interview <span style={{ fontSize: "10px" }}>
                            <FontAwesomeIcon icon={faChevronDown} style={{ marginRight: "8px" }} />
                        </span>
                    </span>
                    <span style={{ ...navStyles.linkStyle, ...navStyles.dropdownStyle, color: "#ffa116" }}>
                        Store <span style={{ fontSize: "10px" }}>
                            <FontAwesomeIcon icon={faChevronDown} style={{ marginRight: "8px" }} />
                        </span>
                    </span>
                </div>
            </div>

            <div style={navStyles.rightSectionStyle}>
                <div style={navStyles.searchBarStyle}>
                    <FontAwesomeIcon icon={faMagnifyingGlass} style={{ marginRight: "8px" }} /> Search
                </div>
                <button style={navStyles.premiumButtonStyle}>Premium</button>
            </div>
        </nav>
    );
};

export default NavBar;