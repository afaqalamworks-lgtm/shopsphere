const Cart = {
  key: 'cart',
  read() {
    try { return JSON.parse(localStorage.getItem(this.key)) || []; } catch (e) { return []; }
  },
  write(items) {
    localStorage.setItem(this.key, JSON.stringify(items));
    this.badge();
  },
  add(p) {
    const items = this.read();
    const found = items.find(i => i.id === p.id);
    if (found) { found.qty = Math.min(found.qty + 1, p.stock); }
    else { items.push({ id: p.id, name: p.name, price: p.price, imageUrl: p.imageUrl, stock: p.stock, qty: 1 }); }
    this.write(items);
  },
  setQty(id, qty) {
    let items = this.read();
    const item = items.find(i => i.id === id);
    if (!item) return;
    if (qty < 1) { items = items.filter(i => i.id !== id); }
    else { item.qty = Math.min(qty, item.stock); }
    this.write(items);
  },
  remove(id) { this.write(this.read().filter(i => i.id !== id)); },
  clear() { this.write([]); },
  count() { return this.read().reduce((n, i) => n + i.qty, 0); },
  total() { return this.read().reduce((sum, i) => sum + i.price * i.qty, 0); },
  badge() {
    const el = document.getElementById('cartCount');
    if (el) el.textContent = this.count();
  }
};

function toast(message) {
  const t = document.createElement('div');
  t.className = 'toast';
  t.textContent = message;
  document.body.append(t);
  setTimeout(() => t.remove(), 2200);
}

document.addEventListener('DOMContentLoaded', () => Cart.badge());