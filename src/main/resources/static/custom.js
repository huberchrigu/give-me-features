import {LitElement, html, css} from 'https://cdn.jsdelivr.net/gh/lit/dist@3/core/lit-core.min.js';

export class Feature extends LitElement {
    static styles = css`
        margin-top: 20px;

        h2 {
            margin-top: 0;
        }

        ul {
            list-style: none;
            padding: 0;
            margin: 0;
        }

        ul li {
            margin-bottom: 10px;
        }

        ul li a {
            text-decoration: none;
            color: #337ab7;
        }

        ul li a:hover {
            text-decoration: underline;
        }
    `;

    static properties = {
        name: {type: String},
        description: {type: String}
    }

    constructor() {
        super();
    }

    render() {
        return html`
            <h2>${this.name}</h2>
            <p>${this.description}</p>
        `;
    }
}

customElements.define('gmf-feature', Feature);
