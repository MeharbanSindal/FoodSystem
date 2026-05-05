package com.foodexpress.util;

import com.foodexpress.model.Order;
import com.foodexpress.model.OrderItem;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.util.Date;

public class PdfGenerator {

    public static void generateInvoice(Order order, OutputStream out) throws Exception {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // ✅ Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("FOOD EXPRESS - INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);
        document.add(new Paragraph(" "));

        // ✅ Order Details (Null Safe)
        document.add(new Paragraph("Order ID: " + (order.getId() != null ? order.getId() : "N/A")));
        document.add(new Paragraph("Customer: " + 
            (order.getUser() != null ? order.getUser().getFullName() : "Guest")));
        document.add(new Paragraph("Date: " + new Date()));
        document.add(new Paragraph("Status: " + 
            (order.getStatus() != null ? order.getStatus() : "N/A")));

        document.add(new Paragraph(" "));

        // ✅ Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        table.addCell("Item");
        table.addCell("Price");
        table.addCell("Qty");
        table.addCell("Total");

        if (order.getItems() != null && !order.getItems().isEmpty()) {

            for (OrderItem item : order.getItems()) {

                table.addCell(item.getProduct() != null ? item.getProduct().getName() : "Item");
                table.addCell("$" + item.getPrice());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell("$" + (item.getPrice() * item.getQuantity()));
            }

        } else {
            table.addCell("No Items");
            table.addCell("-");
            table.addCell("-");
            table.addCell("-");
        }

        document.add(table);

        // ✅ Summary
        document.add(new Paragraph(" "));

        Paragraph summary = new Paragraph();
        summary.setAlignment(Element.ALIGN_RIGHT);

        summary.add("Subtotal: $" + safe(order.getSubtotal()) + "\n");
        summary.add("GST (5%): $" + safe(order.getGst()) + "\n");
        summary.add("Delivery: $" + safe(order.getDeliveryCharge()) + "\n");

        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Paragraph total = new Paragraph(
                "Grand Total: $" + safe(order.getTotalAmount()), totalFont);

        document.add(summary);
        document.add(total);

        // ✅ Footer
        document.add(new Paragraph("\nThank you for ordering with Food Express!"));

        document.close();
    }

    // ✅ Helper method (Null safe double)
    private static double safe(Double value) {
        return value != null ? value : 0.0;
    }
}