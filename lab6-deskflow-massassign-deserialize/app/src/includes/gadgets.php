<?php

/**
 * Writes a small text report to disk when the object is destroyed, so
 * call sites don't need to remember to flush/close a file handle explicitly -
 * just build one of these and let it fall out of scope.
 *
 * Used by the ticket audit-trail export feature (see tickets/export.php).
 */
class AuditReport {
    public $path;
    public $content;

    public function __construct($path = '', $content = '') {
        $this->path = $path;
        $this->content = $content;
    }

    public function __destruct() {
        file_put_contents($this->path, $this->content);
    }
}
