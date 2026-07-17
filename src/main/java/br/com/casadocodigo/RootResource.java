package br.com.casadocodigo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String landingPage() {
        return """
            <!DOCTYPE html>
            <html lang=\"en\">
            <head>
                <meta charset=\"UTF-8\">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                <title>SRV Produto API Explorer</title>
                <style>
                    :root { color-scheme: light; --bg: #f8f9fa; --card: #ffffff; --ink: #202124; --muted: #5f6368; --accent: #1a73e8; --accent-soft: #e8f0fe; --border: #dadce0; }
                    * { box-sizing: border-box; }
                    body { margin: 0; font-family: Arial, Helvetica, sans-serif; background: linear-gradient(135deg, #eef3ff 0%, var(--bg) 70%); color: var(--ink); }
                    .shell { max-width: 980px; margin: 0 auto; padding: 56px 24px 80px; }
                    .hero { background: var(--card); border: 1px solid var(--border); border-radius: 24px; box-shadow: 0 8px 30px rgba(32, 33, 36, 0.08); padding: 32px; }
                    .brand { display: flex; align-items: center; gap: 12px; font-size: 1.1rem; color: var(--accent); font-weight: 700; letter-spacing: 0.02em; }
                    .brand .dot { width: 12px; height: 12px; border-radius: 50%; background: linear-gradient(135deg, #1a73e8, #34a853); box-shadow: 0 2px 8px rgba(26,115,232,0.3); }
                    h1 { margin: 20px 0 10px; font-size: 2.1rem; }
                    .subtitle { margin: 0 0 24px; color: var(--muted); font-size: 1rem; }
                    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin-top: 24px; }
                    .card { border: 1px solid var(--border); border-radius: 16px; padding: 18px; background: #fcfdff; }
                    .card h2 { margin: 0 0 8px; font-size: 1rem; }
                    .card p { margin: 0 0 10px; color: var(--muted); font-size: 0.95rem; }
                    .pill { display: inline-block; margin-top: 6px; padding: 6px 10px; border-radius: 999px; background: var(--accent-soft); color: var(--accent); font-size: 0.84rem; font-weight: 700; }
                    code { font-family: ui-monospace, SFMono-Regular, Menlo, monospace; background: #f1f3f4; padding: 2px 6px; border-radius: 6px; }
                    .actions { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 18px; }
                    a.button, button.button { text-decoration: none; color: white; background: var(--accent); padding: 10px 14px; border-radius: 999px; font-weight: 700; border: 0; cursor: pointer; }
                    a.button.secondary, button.button.secondary { background: #5f6368; }
                    form { display: grid; gap: 10px; margin-top: 14px; }
                    label { display: grid; gap: 6px; font-weight: 600; color: var(--ink); }
                    input, textarea { border: 1px solid var(--border); border-radius: 10px; padding: 10px 12px; font: inherit; }
                    pre { margin-top: 12px; background: #f8f9fa; padding: 12px; border-radius: 12px; border: 1px solid var(--border); white-space: pre-wrap; word-break: break-word; }
                </style>
            </head>
            <body>
                <div class=\"shell\">
                    <section class=\"hero\">
                        <div class=\"brand\"><span class=\"dot\"></span> SRV Produto API Explorer</div>
                        <h1>Discover the services behind the Quarkus backend</h1>
                        <p class=\"subtitle\">A polished overview of the app’s current REST endpoints and health surface.</p>

                        <div class=\"actions\">
                            <a class=\"button\" href=\"/health\">Health check</a>
                            <a class=\"button secondary\" href=\"/hello\">Hello endpoint</a>
                        </div>

                        <div class=\"grid\">
                            <article class=\"card\">
                                <h2>Health</h2>
                                <p>Returns service status for the running Quarkus app.</p>
                                <code>/health</code>
                                <span class=\"pill\">GET</span>
                            </article>
                            <article class=\"card\">
                                <h2>Hello</h2>
                                <p>Returns a simple greeting payload from the API.</p>
                                <code>/hello</code>
                                <span class=\"pill\">GET</span>
                            </article>
                            <article class=\"card\">
                                <h2>Products</h2>
                                <p>Lists and creates products in the bookstore catalog.</p>
                                <code>/produtos</code>
                                <span class=\"pill\">GET · POST</span>
                            </article>
                        </div>

                        <div class=\"card\" style=\"margin-top: 20px;\">
                            <h2>Create product</h2>
                            <p>Submit a new product using the POST verb against /produtos.</p>
                            <form id=\"product-form\">
                                <label>Title
                                    <input name=\"titulo\" type=\"text\" required placeholder=\"Clean Architecture\">
                                </label>
                                <label>Description
                                    <textarea name=\"descricao\" required rows=\"3\" placeholder=\"A practical guide\"></textarea>
                                </label>
                                <label>Pages
                                    <input name=\"paginas\" type=\"number\" min=\"1\" required placeholder=\"240\">
                                </label>
                                <div class=\"actions\">
                                    <button class=\"button\" type=\"submit\">Create via POST</button>
                                    <button class=\"button secondary\" type=\"button\" id=\"list-products\">List via GET</button>
                                </div>
                            </form>
                            <pre id=\"product-output\">Waiting for a request…</pre>
                        </div>
                    </section>
                </div>
                <script>
                    const form = document.getElementById('product-form');
                    const output = document.getElementById('product-output');
                    const listButton = document.getElementById('list-products');

                    async function callProducts(method, payload) {
                        output.textContent = 'Loading...';
                        try {
                            const response = await fetch('/produtos', {
                                method,
                                headers: payload ? { 'Content-Type': 'application/json' } : undefined,
                                body: payload ? JSON.stringify(payload) : undefined
                            });
                            const bodyText = await response.text();
                            output.textContent = bodyText || '(empty response)';
                        } catch (error) {
                            output.textContent = 'Request failed: ' + error.message;
                        }
                    }

                    form.addEventListener('submit', (event) => {
                        event.preventDefault();
                        const payload = {
                            titulo: form.titulo.value,
                            descricao: form.descricao.value,
                            paginas: Number(form.paginas.value)
                        };
                        callProducts('POST', payload);
                    });

                    listButton.addEventListener('click', () => callProducts('GET'));
                </script>
            </body>
            </html>
            """;
    }
}
